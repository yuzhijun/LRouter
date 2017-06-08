// IRemoteRouterAIDL.aidl
package com.lenovohit.lrouter_api;
import com.lenovohit.lrouter_api.core.LRouterRequest;

interface IRemoteRouterAIDL {
    boolean stopRouter(String processName);
    boolean checkIfLocalRouterAsync(String processName,in LRouterRequest routerRequset);
    String navigation(String processName,in LRouterRequest routerRequest);
}
