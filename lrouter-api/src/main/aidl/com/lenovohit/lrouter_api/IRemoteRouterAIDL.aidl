// IRemoteRouterAIDL.aidl
package com.lenovohit.lrouter_api;


interface IRemoteRouterAIDL {
    boolean checkIfLocalRouterAsync(String processName,String routerRequset);
    String navigation(String processName,String routerRequest);
}
