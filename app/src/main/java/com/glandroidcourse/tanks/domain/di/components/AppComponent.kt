package com.glandroidcourse.tanks.domain.di.components

import com.glandroidcourse.tanks.domain.di.modules.NetModule
import com.glandroidcourse.tanks.presentation.auth.AuthorizationFragment
import com.glandroidcourse.tanks.presentation.auth.RegistrationFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetModule::class
])
interface AppComponent {
    fun inject(target: AuthorizationFragment)
    fun inject(target: RegistrationFragment)
}