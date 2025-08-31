import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.methods.response.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


public class sinkMain {

    public static final String sz_const_contract_USDT = "0xdac17f958d2ee523a2206206994597c13d831ec7" ;
    public static final String sz_const_contract_USDC = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48" ;

    // replace with your own API endpoint
    public static String test_data_url_infura = "https://mainnet.infura.io/v3/69696969696969696901730169abcdef" ;
    // replace with your own Tx to parse
    public static String test_data_sz_transactionHash = "0x24009a297b20ee6668f7506a7c497b7bc6917bc387a197a2aa13a7db01022aef" ; // qik sample for USDC
    public static String test_data_sz_transactionHash = "0x60fbd2080c1e4cb4ce9aecfda03d81fdde706c7a8153b71372bbea1f9d8f35b3" ; // qik sample for USDT

    static void cb_proc_tx( Transaction my_tx ) {
        String sz_contract_to_assert = sz_const_contract_USDT ;
        String sz_Tx_to = my_tx.getTo() ;
        System.out.println("To : " + sz_Tx_to );
        if ( ! sz_Tx_to.equalsIgnoreCase( sz_contract_to_assert ) ) { throw new RuntimeException("wrong token"); }
    }
    public static void cb_proc_txRcpt( TransactionReceipt my_tx_rcpt ) {
        List<Log> logs = my_tx_rcpt.getLogs() ;
        for (Log log : logs) {
            List<String> topics = log.getTopics();
            String from = "0x" + topics.get(1).substring(26);
            String to = "0x" + topics.get(2).substring(26);
            BigInteger value = new BigInteger(log.getData().substring(2), 16);
            System.out.println("Transfer from " + from + " to " + to + " of " + value +"/1_000_000");
        }
    }
    public static void main(String[] args) {
        Web3j web3 = Web3j.build(new HttpService( test_data_url_infura ));
        String sz_transactionHash = test_data_sz_transactionHash ;
        try {
            EthTransaction ethTransaction = web3.ethGetTransactionByHash( sz_transactionHash ).send();
            Optional<Transaction> opt_tx = ethTransaction.getTransaction() ;
            opt_tx.ifPresent( my_tx -> cb_proc_tx(my_tx));

            EthGetTransactionReceipt eth_Tx_receipt =
                    web3.ethGetTransactionReceipt( sz_transactionHash ).send();
            Optional<TransactionReceipt> opt_tx_rcpt = eth_Tx_receipt.getTransactionReceipt() ;
            opt_tx_rcpt.ifPresent( my_tx_rcpt -> cb_proc_txRcpt( my_tx_rcpt ));
        } catch (IOException e) { System.out.println("IOEx"); }
        web3.shutdown();
        return;
    }
}
