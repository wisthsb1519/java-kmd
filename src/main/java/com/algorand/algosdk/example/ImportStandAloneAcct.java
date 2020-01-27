package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.auth.ApiKeyAuth;
import com.algorand.algosdk.kmd.client.model.APIV1GETWalletsResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyImportResponse;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.ExportMasterKeyRequest;
import com.algorand.algosdk.kmd.client.model.ImportKeyRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.crypto.Address;


public class ImportStandAloneAcct {
    public static void main(String args[]) throws Exception {
        // Get the values for the following two settings in the
        // kmd.net and kmd.token files within the data directory
        // of your node.
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
                if (wal.getName().equals("wallet28")) {
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
                //create REST request to create new key with wallet token
                // GenerateKeyRequest genAcc = new GenerateKeyRequest();
                // genAcc.setWalletHandleToken(token);
                
                // generate account using algosdk
                Account newAccount = new Account();
                 //Get the new account address
                Address addr = newAccount.getAddress();
                //Get the backup phrase
                String backup = newAccount.toMnemonic();


                System.out.println("Account Address: " + addr.toString());
                System.out.println("Backup Phrase: " + backup);
                System.out.println("New Address = " + newAccount);

                byte[] pk = Mnemonic.toKey(backup);
                System.out.println("Account Private Key = " + pk);

                ImportKeyRequest impResponse = new ImportKeyRequest();
                impResponse.setPrivateKey(pk);
                impResponse.setWalletHandleToken(token);

                APIV1POSTKeyImportResponse impRequest = kmdApiInstance.importKey(impResponse);
                System.out.println("Account Imported = " + impRequest);               

                // APIV1POSTKeyImportResponse impResponse = kmdApiInstance.importKey(newAccount.privatekey, token);



            }else{
                System.out.println("Did not Find Wallet");
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}