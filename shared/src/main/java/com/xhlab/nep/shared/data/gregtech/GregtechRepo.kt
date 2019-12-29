package com.xhlab.nep.shared.data.gregtech

interface GregtechRepo {
    suspend fun insertGregtechMachine(machineName: String): Int
    suspend fun deleteGregtechMachines()
}