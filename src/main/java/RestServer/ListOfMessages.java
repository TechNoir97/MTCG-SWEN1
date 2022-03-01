package RestServer;

import java.util.*;

public class ListOfMessages {

    public List<Message> messageList;



    public ListOfMessages(){

        messageList=new ArrayList<>();
    }


    public void putMessageIntoList(Message message)
    {
        messageList.add(message);

    }
    public String getMessage(int index)
    {
        if(index<0 || index>(messageList.size()-1)){
            return "ERROR";
        }
        Message temp= messageList.get(index);
        return temp.myBody;

    }
    public String getAllMessages()
    {
        StringBuilder messages = new StringBuilder();
        for(int i=0; i<messageList.size() ; i++){
            Message temp= messageList.get(i);
            messages.append(temp.myBody+ "\r\n");
        }
        return messages.toString();

    }
    public boolean updateMessage(int index, Message message){



        if(index<0 || index>(messageList.size()-1)) {return false;}
        else
        messageList.set(index, message);
        return true;
    }
    public boolean removeMessage(int index){
        if(index<0 || index>(messageList.size()-1)) {return false;}
        else
        messageList.remove(index);
        return true;
    }


}
