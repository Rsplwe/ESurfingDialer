package com.rsplwe.esurfing.cipher

import com.rsplwe.esurfing.cipher.impl.*

object CipherFactory {

    fun getInstance(type: String): CipherInterface {
        return when (type) {
            "CAFBCBAD-B6E7-4CAB-8A67-14D39F00CE1E" -> AESCBC(
                key1 = KeyData.key1_CAFBCBAD_B6E7_4CAB_8A67_14D39F00CE1E,
                key2 = KeyData.key2_CAFBCBAD_B6E7_4CAB_8A67_14D39F00CE1E,
                iv = KeyData.iv_CAFBCBAD_B6E7_4CAB_8A67_14D39F00CE1E
            )

            "A474B1C2-3DE0-4EA2-8C5F-7093409CE6C4" -> AESECB(
                key1 = KeyData.key1_A474B1C2_3DE0_4EA2_8C5F_7093409CE6C4,
                key2 = KeyData.key2_A474B1C2_3DE0_4EA2_8C5F_7093409CE6C4
            )

            "5BFBA864-BBA9-42DB-8EAD-49B5F412BD81" -> DESedeCBC(
                key1 = KeyData.key1_5BFBA864_BBA9_42DB_8EAD_49B5F412BD81,
                key2 = KeyData.key2_5BFBA864_BBA9_42DB_8EAD_49B5F412BD81,
                iv = KeyData.iv_5BFBA864_BBA9_42DB_8EAD_49B5F412BD81
            )

            "6E0B65FF-0B5B-459C-8FCE-EC7F2BEA9FF5" -> DESedeECB(
                key1 = KeyData.key1_6E0B65FF_0B5B_459C_8FCE_EC7F2BEA9FF5,
                key2 = KeyData.key2_6E0B65FF_0B5B_459C_8FCE_EC7F2BEA9FF5
            )

            "B809531F-0007-4B5B-923B-4BD560398113" -> ZUC(
                key = KeyData.key_B809531F_0007_4B5B_923B_4BD560398113,
                iv = KeyData.iv_B809531F_0007_4B5B_923B_4BD560398113
            )

            "F3974434-C0DD-4C20-9E87-DDB6814A1C48" -> SM4CBC(
                key = KeyData.key_F3974434_C0DD_4C20_9E87_DDB6814A1C48,
                iv = KeyData.iv_F3974434_C0DD_4C20_9E87_DDB6814A1C48
            )

            "ED382482-F72C-4C41-A76D-28EEA0F1F2AF" -> SM4ECB(
                key = KeyData.key_ED382482_F72C_4C41_A76D_28EEA0F1F2AF
            )

            else -> throw IllegalArgumentException("Unknown algorithm: $type")
        }
    }

}