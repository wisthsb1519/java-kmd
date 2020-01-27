package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.auth.ApiKeyAuth;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyExportResponse;
import com.algorand.algosdk.kmd.client.model.APIV1GETWalletsResponse;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.kmd.client.model.ExportKeyRequest;
import com.algorand.algosdk.mnemonic.Mnemonic;

import org.bouncycastle.util.Arrays;

import com.algorand.algosdk.kmd.client.model.APIV1Wallet;

public class BackUpKmdAcct {
    public static void main(String args[]) throws Exception {
        //Get the values for the following two settings in the
        //kmd.net and kmd.token files within the data directory 
        //of your node.        
        final String KMD_API_ADDR = "http://localhost:4002";
        final String KMD_API_TOKEN = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        // Create a wallet with kmd rest api
        KmdClient client = new KmdClient();
        client.setBasePath(KMD_API_ADDR);
        // Configure API key authorization: api_key
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(KMD_API_TOKEN);
        KmdApi kmdApiInstance = new KmdApi(client);

        APIV1GETWalletsResponse wallets;
        String walletId = null;
        try {
            // Get all wallets from kmd
            // Loop through them and find the one we
            // are interested in them
            wallets = kmdApiInstance.listWallets();
            for (APIV1Wallet wal : wallets.getWallets()) {
                System.out.println(wal.getName());
                if (wal.getName().equals("wallet29")) {
                    walletId = wal.getId();
                    break;
                }
            }
            if (walletId != null) {
                // create REST request to get wallet token
                InitWalletHandleTokenRequest walletHandleRequest = new InitWalletHandleTokenRequest();
                walletHandleRequest.setWalletId(walletId);
                walletHandleRequest.setWalletPassword("test");
                // execute request to get the wallet token
                String token = kmdApiInstance.initWalletHandleToken(walletHandleRequest).getWalletHandleToken();            
                ExportKeyRequest expRequest = new ExportKeyRequest();
                expRequest.setAddress("VTYAKVWFGO5VJFE4G6A7CCSLLVHIBDP3VG2O56CYG7V7RMYESNSG4MF23Y");
                expRequest.setWalletHandleToken(token);
                expRequest.walletPassword("test");

                APIV1POSTKeyExportResponse expResponse = kmdApiInstance.exportKey(expRequest);
                byte [] expResponseSlice = Arrays.copyOfRange(expResponse.getPrivateKey(), 0, 32);
                System.out.println("This is the expResponse: " + expResponse);
                System.out.println("This is the expResponseSlice: " + expResponseSlice);
                // System.out.println("This is the expResponse: " + new String(expResponse.getPrivateKey()));
                String mnem = Mnemonic.fromKey(expResponseSlice);
                // String mnem = Account.toMnemonic(expResponse);
            
                System.out.println("Backup Phrase = " + mnem);

            }else{
                System.out.println("Did not Find Wallet");
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}