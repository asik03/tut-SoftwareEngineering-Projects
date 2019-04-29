package rest_api;

public class Status {
    private static Boolean ready = false;
    private static Boolean executing = false;

    private Status(){
        
    }
	
	public static boolean getReady() {
        return ready;
    }
	public static boolean getExecuting() {
        return executing;
    }
	public static void setExecuting(boolean exec) {
        executing = exec;
    }
	public static void setReady(boolean rdy) {
        ready = rdy;
	}
	

}