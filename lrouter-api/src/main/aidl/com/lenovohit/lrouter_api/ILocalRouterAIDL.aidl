// ILocalRouterAIDL.aidl
package com.lenovohit.lrouter_api;
import com.lenovohit.lrouter_api.core.LRouterRequest;

interface ILocalRouterAIDL {
    void connectRemoteRouter(String processName);
    boolean stopRemoteRouter();
    boolean checkIfLocalRouterAsync(in LRouterRequest routerRequset);
    String navigation(in LRouterRequest routerRequest);
}
