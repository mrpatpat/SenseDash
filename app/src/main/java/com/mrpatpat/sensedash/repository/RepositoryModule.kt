package com.mrpatpat.sensedash.repository

import org.koin.dsl.module

val repositoryModule = module {
    single{ DiscoveryMessageRepository(get()) }
    single{ DiscoveryStateRepository(get()) }
    single{ AdvertisementStateRepository(get()) }
    single{ AdvertisementMessageRepository(get()) }
}