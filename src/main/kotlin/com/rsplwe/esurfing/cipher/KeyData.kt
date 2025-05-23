package com.rsplwe.esurfing.cipher

object KeyData {

    // -----------------------------------------------------------------
    // Algo Id: CAFBCBAD-B6E7-4CAB-8A67-14D39F00CE1E (AES/CBC/NoPadding)
    // -----------------------------------------------------------------
    val key1_CAFBCBAD_B6E7_4CAB_8A67_14D39F00CE1E = byteArrayOf(
        0x55, 0x48, 0x5B, 0x7A, 0x7C, 0x6D, 0x3E, 0x2A, 0x6C, 0x56, 0x4D, 0x2D, 0x22, 0x67, 0x56, 0x4D
    )
    val key2_CAFBCBAD_B6E7_4CAB_8A67_14D39F00CE1E = byteArrayOf(
        0x4E, 0x25, 0x53, 0x71, 0x5F, 0x7A, 0x5A, 0x5C, 0x60, 0x45, 0x63, 0x48, 0x66, 0x24, 0x65, 0x50
    )
    val iv_CAFBCBAD_B6E7_4CAB_8A67_14D39F00CE1E = byteArrayOf(
        0x54, 0x67, 0x70, 0x75, 0x60, 0x73, 0x5A, 0x5C, 0x69, 0x40, 0x42, 0x66, 0x73, 0x5A, 0x7D, 0x5E
    )

    // -----------------------------------------------------------------
    // Algo Id: A474B1C2-3DE0-4EA2-8C5F-7093409CE6C4 (AES/ECB/NoPadding)
    // -----------------------------------------------------------------
    val key1_A474B1C2_3DE0_4EA2_8C5F_7093409CE6C4 = byteArrayOf(
        0x3A, 0x71, 0x7C, 0x4C, 0x51, 0x4F, 0x3C, 0x6A, 0x2E, 0x43, 0x7A, 0x43, 0x3B, 0x56, 0x57, 0x59
    )
    val key2_A474B1C2_3DE0_4EA2_8C5F_7093409CE6C4 = byteArrayOf(
        0x72, 0x6E, 0x25, 0x41, 0x45, 0x2F, 0x41, 0x54, 0x27, 0x4B, 0x3B, 0x3B, 0x59, 0x25, 0x52, 0x24
    )

    // ----------------------------------------------------------------
    // Algo Id: 5BFBA864-BBA9-42DB-8EAD-49B5F412BD81 (DESede/CBC/NoPadding)
    // -----------------------------------------------------------------
    val key1_5BFBA864_BBA9_42DB_8EAD_49B5F412BD81 = byteArrayOf(
        0x5E, 0x67, 0x72, 0x79, 0x28, 0x50, 0x47, 0x75, 0x6D, 0x48, 0x63, 0x74,
        0x5D, 0x29, 0x21, 0x3C, 0x7E, 0x6B, 0x56, 0x29, 0x4F, 0x21, 0x52, 0x40
    )
    val key2_5BFBA864_BBA9_42DB_8EAD_49B5F412BD81 = byteArrayOf(
        0x63, 0x73, 0x63, 0x26, 0x72, 0x5C, 0x5E, 0x73, 0x6B, 0x60, 0x74, 0x51,
        0x7B, 0x74, 0x76, 0x7D, 0x3F, 0x59, 0x2E, 0x6D, 0x6F, 0x64, 0x3E, 0x69
    )
    val iv_5BFBA864_BBA9_42DB_8EAD_49B5F412BD81 = byteArrayOf(
        0x77, 0x2D, 0x56, 0x51, 0x28, 0x49, 0x7E, 0x57
    )

    // ----------------------------------------------------------------
    // Algo Id: 6E0B65FF-0B5B-459C-8FCE-EC7F2BEA9FF5 (DESede/ECB/NoPadding)
    // -----------------------------------------------------------------
    val key1_6E0B65FF_0B5B_459C_8FCE_EC7F2BEA9FF5 = byteArrayOf(
        0x25, 0x6A, 0x63, 0x5A, 0x46, 0x3F, 0x26, 0x64, 0x53, 0x7A, 0x2E, 0x5B,
        0x24, 0x4C, 0x62, 0x67, 0x2B, 0x2D, 0x67, 0x68, 0x43, 0x74, 0x69, 0x51
    )
    val key2_6E0B65FF_0B5B_459C_8FCE_EC7F2BEA9FF5 = byteArrayOf(
        0x59, 0x28, 0x5B, 0x7E, 0x7D, 0x26, 0x74, 0x49, 0x48, 0x76, 0x59, 0x58,
        0x62, 0x75, 0x51, 0x55, 0x26, 0x73, 0x55, 0x5C, 0x67, 0x52, 0x2E, 0x6C
    )

    // ----------------------------------------------------------------
    //  Algo Id: B809531F-0007-4B5B-923B-4BD560398113 (ZUC-128)
    // ----------------------------------------------------------------
    val key_B809531F_0007_4B5B_923B_4BD560398113 = byteArrayOf(
        0x4f, 0x3f, 0x25, 0x70, 0x53, 0x2b, 0x4b, 0x59, 0x3b, 0x5d, 0x5b, 0x21, 0x3a, 0x41, 0x7a, 0x48
    )
    val iv_B809531F_0007_4B5B_923B_4BD560398113 = byteArrayOf(
        0x41, 0x3c, 0x7a, 0x55, 0x4a, 0x21, 0x48, 0x3d, 0x5d, 0x2d, 0x24, 0x45, 0x45, 0x3c, 0x57, 0x79
    )

    // ----------------------------------------------------------------
    //  Algo Id: F3974434-C0DD-4C20-9E87-DDB6814A1C48 (SM4/CBC/PKCS5Padding)
    // ----------------------------------------------------------------
    val key_F3974434_C0DD_4C20_9E87_DDB6814A1C48 = byteArrayOf(
        0x28, 0x2f, 0x29, 0x25, 0x6f, 0x3c, 0x75, 0x48, 0x6d, 0x4c, 0x2e, 0x51, 0x55, 0x27, 0x22, 0x2d
    )
    val iv_F3974434_C0DD_4C20_9E87_DDB6814A1C48 = byteArrayOf(
        0x68, 0x3c, 0x42, 0x51, 0x5a, 0x46, 0x3a, 0x52, 0x67, 0x77, 0x7e, 0x6e, 0x69, 0x70, 0x48, 0x5e
    )

    // ----------------------------------------------------------------
    //  Algo Id: ED382482-F72C-4C41-A76D-28EEA0F1F2AF
    // ----------------------------------------------------------------
    val key_ED382482_F72C_4C41_A76D_28EEA0F1F2AF = byteArrayOf(
        0x53, 0x2f, 0x79, 0x4a, 0x4e, 0x79, 0x74, 0x4d, 0x67, 0x66, 0x57, 0x5a, 0x2d, 0x44, 0x5c, 0x57
    )

    // ----------------------------------------------------------------
    //  Algo Id: B3047D4E-67DF-4864-A6A5-DF9B9E525C79 (XTEA Non-standard)
    // ----------------------------------------------------------------
    val key1_B3047D4E_67DF_4864_A6A5_DF9B9E525C79 = intArrayOf(
        0x7a7a676a, 0x277e4a73, 0x3e43296c, 0x577d7d7a,
    )
    val key2_B3047D4E_67DF_4864_A6A5_DF9B9E525C79 = intArrayOf(
        0x3d3c695f, 0x71797a74, 0x445f5763, 0x6f692765,
    )
    val key3_B3047D4E_67DF_4864_A6A5_DF9B9E525C79 = intArrayOf(
        0x5b5a683d, 0x2e572a77, 0x4a474465, 0x663d7e5c
    )

    // ----------------------------------------------------------------
    //  Algo Id: C32C68F9-CA81-4260-A329-BBAFD1A9CCD1 (XTEA-IV Non-standard)
    // ----------------------------------------------------------------
    val key1_C32C68F9_CA81_4260_A329_BBAFD1A9CCD1 = intArrayOf(
        0x796d7855, 0x297b2355, 0x587d726e, 0x4d3d4423
    )
    val key2_C32C68F9_CA81_4260_A329_BBAFD1A9CCD1 = intArrayOf(
        0x7c70525d, 0x5a585d3d, 0x413e4029, 0x28755d6a
    )
    val key3_C32C68F9_CA81_4260_A329_BBAFD1A9CCD1 = intArrayOf(
        0x425e5f6e, 0x46754e24, 0x507b233d, 0x2d644641
    )
    val iv_C32C68F9_CA81_4260_A329_BBAFD1A9CCD1 = intArrayOf(
        0x544c2f3f, 0x6f485121
    )

}