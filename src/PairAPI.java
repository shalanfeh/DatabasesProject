import ClientAPI.ClientInterface;
import ServerAPI.ServerAbstract;

//Correlates a client and server API instance
public class PairAPI {
    ClientInterface Client;
    ServerAbstract Server;
    PairAPI(ClientInterface NewClient, ServerAbstract NewServer) {
        Client = NewClient;
        Server = NewServer;
    }
}
