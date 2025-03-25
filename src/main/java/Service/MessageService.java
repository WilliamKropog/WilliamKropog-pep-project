package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.List;

public class MessageService {
    
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public Message addMessage(Message message) {
        return messageDAO.insertMessage(message);
    }

    public Message deleteMessageByMessageId(int messageId) {
        return messageDAO.removeMessageByMessageId(messageId);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public List<Message> getAllMessagesByAccountId(int accountId) {
        return messageDAO.getAllMessagesByAccountId(accountId);
    }

    public Message getMessageByMessageId(int messageId) {
        return messageDAO.getMessageByMessageId(messageId);
    }

    public Message updateMessageByMessageId(int messageId, String newMessageText) {
        return messageDAO.updateMessageByMessageId(messageId, newMessageText);
    }
}
