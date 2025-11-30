package org.quijava.quijava.compose.di

import org.koin.dsl.module
import org.quijava.quijava.compose.viewmodels.LoginViewModel
import org.quijava.quijava.compose.viewmodels.RegisterViewModel
import org.quijava.quijava.services.LoginService
import org.quijava.quijava.services.RegisterService
import org.springframework.context.ConfigurableApplicationContext

fun authModule(springContext: ConfigurableApplicationContext) = module {
    // Services
    single { springContext.getBean(LoginService::class.java) }
    single { springContext.getBean(RegisterService::class.java) }
    
    // ViewModels
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }
}
