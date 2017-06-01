// ILocalRouterAIDL.aidl
package com.lenovohit.lrouter_api;

interface ILocalRouterAIDL {
    boolean checkIfLocalRouterAsync(String routerRequset);
    void connectRemoteRouter(String processName);
    String navigation(String routerRequest);
}
