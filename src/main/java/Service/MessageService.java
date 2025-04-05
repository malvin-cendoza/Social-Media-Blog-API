package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for messages.
 * Handles validation and operations for creating, retrieving, updating, and deleting messages.
 */
public class MessageService {

    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    // Adds a message after validation checks (e.g., user must exist, message must be valid)
    public Message createMessage(Message message) {
        boolean isTextValid = message.getMessage_text() != null &&
                              !message.getMessage_text().isBlank() &&
                              message.getMessage_text().length() <= 255;

        boolean isUserValid = accountDAO.getAccountById(message.getPosted_by()).isPresent();

        if (isTextValid && isUserValid) {
            return messageDAO.insertMessage(message);
        }
        return null;
    }

    // Gets all messages
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    // Gets a message by ID
    public Optional<Message> getMessageById(int id) {
        return messageDAO.getMessageById(id);
    }

    // Deletes a message by ID
    public Optional<Message> deleteMessageById(int id) {
        return messageDAO.deleteMessageById(id);
    }

    // Updates a message's text
    public Optional<Message> updateMessage(int id, String newText) {
        boolean isValid = newText != null && !newText.isBlank() && newText.length() <= 255;
        if (!isValid) {
            return Optional.empty();
        }

        return messageDAO.updateMessageText(id, newText);
    }

    // Gets all messages posted by a specific user
    public List<Message> getMessagesByAccountId(int accountId) {
        return messageDAO.getMessagesByAccountId(accountId);
    }
}