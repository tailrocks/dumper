/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhokhov.dumper.api.security;

import at.favre.lib.bytes.BinaryToTextEncoding;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AesGcmKeyManager;

import java.nio.ByteOrder;
import java.security.GeneralSecurityException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EncryptionService {

    public static void test(String plaintext) throws GeneralSecurityException {
        AeadConfig.register();

        // 1. Generate the key material.
        KeysetHandle keysetHandle = KeysetHandle.generateNew(
                AesGcmKeyManager.aes128GcmTemplate());

        String associatedData = "Tink";

        // 2. Get the primitive.
        Aead aead = keysetHandle.getPrimitive(Aead.class);

        // 3. Use the primitive to encrypt a plaintext,
        byte[] ciphertext = aead.encrypt(plaintext.getBytes(UTF_8), associatedData.getBytes(UTF_8));

        // ... or to decrypt a ciphertext.
        byte[] decrypted = aead.decrypt(ciphertext, associatedData.getBytes(UTF_8));

        BinaryToTextEncoding.Hex hex = new BinaryToTextEncoding.Hex();
        String hexCode = hex.encode(ciphertext, ByteOrder.BIG_ENDIAN);

        byte[] bytes = hex.decode(hexCode);

        byte[] decrypted2 = aead.decrypt(bytes, associatedData.getBytes(UTF_8));

        String decryptedPass = new String(decrypted2, UTF_8);

        System.out.println("X");
    }
}
