package API;

import API.ClientAPI.ClientInterface;
import API.ServerAPI.ServerAbstract;

//Correlates a client and server API instance
public class PairAPI {
    public ClientInterface Client;
    public ServerAbstract Server;
    PairAPI(ClientInterface NewClient, ServerAbstract NewServer) {
        Client = NewClient;
        Server = NewServer;
    }
}
