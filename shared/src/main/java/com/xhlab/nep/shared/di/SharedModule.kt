package com.xhlab.nep.shared.di

import dagger.Module

@Module(includes = [ParserModule::class, RoomModule::class])
class SharedModule