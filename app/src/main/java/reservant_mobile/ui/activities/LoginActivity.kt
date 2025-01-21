package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.LogoWithReturn
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.EmployeeRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.viewmodels.LoginViewModel

@Composable
fun LoginActivity(
    navController: NavHostController,
    onReturnClick: (() -> Unit)? = null
) {
    val loginViewModel = viewModel<LoginViewModel>()

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var formSent by remember { mutableStateOf(false) }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val onBack = onReturnClick ?: { navController.popBackStack() }

            LogoWithReturn { onBack() }

            // ----- LOGIN FIELD -----
            FormInput(
                inputText = loginViewModel.login.value,
                onValueChange = {
                    loginViewModel.login.value = it
                    // Once user types, you could reset formSent or keep it
                    // formSent = false // optional
                },
                label = stringResource(R.string.label_login),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                // Show red error if blank AND user pressed login
                isError = loginViewModel.isLoginInvalid() && formSent,
                errorText = stringResource(R.string.error_field_required),
                formSent = formSent
            )

            // ----- PASSWORD FIELD -----
            FormInput(
                inputText = loginViewModel.password.value,
                onValueChange = {
                    loginViewModel.password.value = it
                    // formSent = false // optional
                },
                label = stringResource(R.string.label_password),
                leadingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(R.string.label_password_visibility)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = loginViewModel.isPasswordInvalid() && formSent,
                errorText = stringResource(R.string.error_field_required),
                formSent = formSent
            )

            // ----- LOGIN BUTTON -----
            ButtonComponent(
                onClick = {
                    loginViewModel.viewModelScope.launch {
                        isLoading = true
                        // Trigger form validation
                        formSent = true

                        // If any field is invalid, do NOT proceed
                        if (loginViewModel.isFormInvalid()) {
                            // remain on screen, show red errors
                            isLoading = false
                            return@launch
                        }

                        // Otherwise attempt login
                        if (loginViewModel.login()) {
                            // success => go to next screen
                            if (Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles) {
                                navController.navigate(EmployeeRoutes.SelectRestaurant)
                            } else {
                                navController.navigate(MainRoutes.Home)
                            }
                        }
                        isLoading = false
                    }
                },
                label = stringResource(R.string.label_login_action),
                isLoading = isLoading
            )

            // Show possible error from server (like invalid credentials)
            ShowErrorToast(context = LocalContext.current, id = loginViewModel.getToastError())

            Spacer(modifier = Modifier.weight(1f))

            // ----- REGISTER BUTTON -----
            ButtonComponent(
                onClick = {
                    if (!isLoading)
                        navController.navigate(AuthRoutes.Register)
                },
                label = stringResource(R.string.label_signup)
            )

            // ----- FORGOT PASSWORD BUTTON -----
            ButtonComponent(
                onClick = {
                    if (!isLoading) {
                        // TODO: Handle password recovery / navigate
                    }
                },
                label = stringResource(R.string.label_password_forgot)
            )
        }
    }
}
