package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.auth.ApiKeyAuth;
import com.algorand.algosdk.kmd.client.model.APIV1GETWalletsResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTMasterKeyExportResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.ExportMasterKeyRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.mnemonic.Mnemonic;

/**
 * Hello world!
 *
 */
public class RestoreKmdWalletRegenAcct {
    public static void main(String args[]) throws Exception {
        //Get the values for the following two settings in the
        //kmd.net and kmd.token files within the data directory 
        //of your node.        
        final String KMD_API_ADDR = "http://localhost:4002";
        final String KMD_API_TOKEN = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        final String BACKUP_PHRASE = "bulk permit blind goat arctic engine sample gallery near canoe barely normal cycle learn grocery armor stable state input empty draft orbit gun above guess";
        // Create a wallet with kmd rest api
        KmdClient client = new KmdClient();
        client.setBasePath(KMD_API_ADDR);
        // Configure API key authorization: api_key
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(KMD_API_TOKEN);
        KmdApi kmdApiInstance = new KmdApi(client);
        byte[] mkd = Mnemonic.toKey(BACKUP_PHRASE);
        APIV1POSTWalletResponse wallet;
        try {
            //create the REST request
            CreateWalletRequest req = new CreateWalletRequest()
                    .walletName("wallet29")
                    .walletPassword("test")
                    .masterDerivationKey(mkd)
                    .walletDriverName("sqlite");
            //create the wallet        
            wallet = kmdApiInstance.createWallet(req);
            String wallName = wallet.getWallet().getName();
            System.out.println("New Address = " + wallName);

            // begin process to generate account
            String wallId = wallet.getWallet().getId();
            //create REST request to get wallet token
            InitWalletHandleTokenRequest walletHandleRequest = new InitWalletHandleTokenRequest();
            walletHandleRequest.setWalletId(wallId);
            walletHandleRequest.setWalletPassword("test");
            //execute request to get the wallet token
            String token = kmdApiInstance.initWalletHandleToken(walletHandleRequest).getWalletHandleToken();
            //create REST request to create new key with wallet token
            GenerateKeyRequest genAcc = new GenerateKeyRequest();
            genAcc.setWalletHandleToken(token);
            //execute request to generate new key(account)
            String newAccount = kmdApiInstance.generateKey(genAcc).getAddress();
            System.out.println("New Address = " + newAccount);

        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}


