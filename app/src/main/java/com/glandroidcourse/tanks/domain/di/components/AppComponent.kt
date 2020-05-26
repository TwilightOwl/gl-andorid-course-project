package com.glandroidcourse.tanks.domain.di.components

import com.glandroidcourse.tanks.domain.di.modules.AppModule
import com.glandroidcourse.tanks.domain.di.modules.NetModule
import com.glandroidcourse.tanks.presentation.auth.AuthorizationFragment
import com.glandroidcourse.tanks.presentation.auth.RegistrationFragment
import com.glandroidcourse.tanks.presentation.game.GameFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetModule::class,
    AppModule::class
])
interface AppComponent {
    fun inject(target: AuthorizationFragment)
    fun inject(target: RegistrationFragment)
    fun inject(target: GameFragment)
}