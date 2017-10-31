package com.netcracker.paladin.infrastructure.services.encryption;

import com.netcracker.paladin.infrastructure.services.encryption.asymmetric.Rsa;
import com.netcracker.paladin.infrastructure.services.encryption.sessionkeygen.ChebiKeygen;
import com.netcracker.paladin.infrastructure.services.encryption.symmetric.Aes;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created on 28.10.14.
 */
@RunWith(value = Parameterized.class)
public class EncryptionServiceImplTest {

    EncryptionService encryptionService = new EncryptionServiceImpl(null,
                                                                    new Rsa(),
                                                                    new Aes(),
                                                                    new ChebiKeygen());

    private String oldPlainText;

    public EncryptionServiceImplTest(String oldPlainText) {
        this.oldPlainText = oldPlainText;
        encryptionService.generatePrivateKey();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Pasha ne molodez"},
                {"Chebi horosho"},
                {"Шифрование отличное"},
        });
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fullCycle() throws Exception {
        byte[] encryptedMail = encryptionService.encryptEmail(oldPlainText, "pasha@gmail.com");
        String newPlainText = encryptionService.decryptEmail(encryptedMail);
        System.out.println("Before: "+oldPlainText);
        System.out.println("After: "+newPlainText);
        assertEquals(oldPlainText, newPlainText);
    }
}