package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRegistered by remember { mutableStateOf(AuthManager.isRegistered(context)) }
    
    // Registration form states
    var regName by remember { mutableStateOf("Vedant") }
    var regEmail by remember { mutableStateOf("vedantmodi1221@gmail.com") }
    var regPassword by remember { mutableStateOf("") }
    var regPin by remember { mutableStateOf("") }
    var showRegPassword by remember { mutableStateOf(false) }

    // Login screen states
    var loginMethod by remember { mutableStateOf("PIN") } // "PIN" or "PASSWORD"
    var enteredPin by remember { mutableStateOf("") }
    var enteredPassword by remember { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }

    val savedName = remember(isRegistered) { AuthManager.getUserName(context) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isRegistered) {
            // REGISTRATION SCREEN
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Logo Icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BrandOnPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SECURITY FIRST",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Setup Security Lock",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Set up your credentials to lock and secure your financial data safely offline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Full Name Input
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    // Email Input
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    // Password Input
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Security Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showRegPassword = !showRegPassword }) {
                                Icon(
                                    imageVector = if (showRegPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showRegPassword) "Hide Password" else "Show Password"
                                )
                            }
                        },
                        visualTransformation = if (showRegPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    // Secure PIN Input (4 Digits)
                    OutlinedTextField(
                        value = regPin,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) regPin = it },
                        label = { Text("4-Digit Quick PIN") },
                        placeholder = { Text("e.g. 1234") },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank() || regPin.length != 4) {
                                Toast.makeText(context, "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show()
                            } else {
                                val success = AuthManager.registerUser(
                                    context = context,
                                    name = regName,
                                    email = regEmail,
                                    password = regPassword,
                                    pin = regPin
                                )
                                if (success) {
                                    Toast.makeText(context, "Setup Successful! Welcome, $regName", Toast.LENGTH_LONG).show()
                                    isRegistered = true
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Registration failed. Verify inputs.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create & Lock App", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        } else {
            // UNLOCK LOCK SCREEN
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Secure header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CardPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "App Locked",
                            tint = BrandOnPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "APP LOCKED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Welcome back, $savedName",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Enter safety credentials to access your financial wallet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 24.dp, top = 4.dp, end = 24.dp)
                    )
                }

                // Selector tabs for PIN vs Password
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (loginMethod == "PIN") CardPurple else Color.Transparent)
                            .clickable { 
                                loginMethod = "PIN"
                                loginError = ""
                                enteredPin = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Pin,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (loginMethod == "PIN") BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Quick PIN",
                                color = if (loginMethod == "PIN") BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (loginMethod == "PASSWORD") CardPurple else Color.Transparent)
                            .clickable { 
                                loginMethod = "PASSWORD"
                                loginError = ""
                                enteredPassword = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (loginMethod == "PASSWORD") BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Password",
                                color = if (loginMethod == "PASSWORD") BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Error feedback
                if (loginError.isNotEmpty()) {
                    Text(
                        text = loginError,
                        color = ColorExpense,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Input Areas depending on selected Unlock Method
                if (loginMethod == "PIN") {
                    // PIN Entry Representation Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        for (i in 0 until 4) {
                            val active = i < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (active) BrandPrimary else MaterialTheme.colorScheme.outlineVariant)
                                    .border(2.dp, if (active) BrandPrimary else MaterialTheme.colorScheme.outline, CircleShape)
                            )
                        }
                    }

                    // Numeric Pad UI
                    Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val row1 = listOf("1", "2", "3")
                        val row2 = listOf("4", "5", "6")
                        val row3 = listOf("7", "8", "9")

                        PinRow(row1, onDigitClick = { digit ->
                            if (enteredPin.length < 4) {
                                enteredPin += digit
                                if (enteredPin.length == 4) {
                                    // Auto-submit PIN
                                    val success = AuthManager.loginWithPin(context, enteredPin)
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        loginError = "Incorrect PIN! Please try again."
                                        enteredPin = ""
                                    }
                                }
                            }
                        })
                        PinRow(row2, onDigitClick = { digit ->
                            if (enteredPin.length < 4) {
                                enteredPin += digit
                                if (enteredPin.length == 4) {
                                    val success = AuthManager.loginWithPin(context, enteredPin)
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        loginError = "Incorrect PIN! Please try again."
                                        enteredPin = ""
                                    }
                                }
                            }
                        })
                        PinRow(row3, onDigitClick = { digit ->
                            if (enteredPin.length < 4) {
                                enteredPin += digit
                                if (enteredPin.length == 4) {
                                    val success = AuthManager.loginWithPin(context, enteredPin)
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        loginError = "Incorrect PIN! Please try again."
                                        enteredPin = ""
                                    }
                                }
                            }
                        })
                        
                        // Last row: Clear, 0, Backspace
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Clear Button
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .clickable { enteredPin = "" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "C",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Digit 0
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable {
                                        if (enteredPin.length < 4) {
                                            enteredPin += "0"
                                            if (enteredPin.length == 4) {
                                                val success = AuthManager.loginWithPin(context, enteredPin)
                                                if (success) {
                                                    onLoginSuccess()
                                                } else {
                                                    loginError = "Incorrect PIN! Please try again."
                                                    enteredPin = ""
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "0",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Backspace Button
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (enteredPin.isNotEmpty()) {
                                            enteredPin = enteredPin.dropLast(1)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Backspace",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Password input field
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val savedEmail = AuthManager.getUserEmail(context)
                            
                            Text(
                                text = "Sign in to account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = savedEmail,
                                onValueChange = {},
                                label = { Text("Account Email") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                readOnly = true,
                                enabled = false
                            )

                            OutlinedTextField(
                                value = enteredPassword,
                                onValueChange = { enteredPassword = it },
                                label = { Text("Enter Security Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                        Icon(
                                            imageVector = if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (showLoginPassword) "Hide" else "Show"
                                        )
                                    }
                                },
                                visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            Button(
                                onClick = {
                                    val success = AuthManager.loginWithPassword(context, savedEmail, enteredPassword)
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        loginError = "Incorrect Password! Please verify and retry."
                                        enteredPassword = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Unlock with Password", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinRow(
    digits: List<String>,
    onDigitClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        digits.forEach { digit ->
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onDigitClick(digit) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = digit,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
