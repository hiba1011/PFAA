package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.academic.Message;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class MessageService {
    private static final List<Message> messages = new ArrayList<>();
    private static final String FILE_PATH = "messages.txt";

    static {
        loadMessages();
    }

    public void sendMessage(Message msg) {
        msg.setId(messages.size() + 1);
        msg.setDate(java.time.LocalDateTime.now());
        messages.add(msg);
        saveMessages();
    }

    public List<Message> getConversation(int user1Id, String user1Type, int user2Id, String user2Type) {
        return messages.stream()
                .filter(m -> (m.getSenderId() == user1Id && m.getSenderType().equals(user1Type)
                        && m.getReceiverId() == user2Id && m.getReceiverType().equals(user2Type))
                        ||
                        (m.getSenderId() == user2Id && m.getSenderType().equals(user2Type)
                                && m.getReceiverId() == user1Id && m.getReceiverType().equals(user1Type)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }

    private static void saveMessages() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Message m : messages) {
                pw.println(m.getId() + "|" + m.getSenderId() + "|" + m.getSenderType() + "|" +
                        m.getReceiverId() + "|" + m.getReceiverType() + "|" +
                        m.getDate() + "|" + m.getContent().replace("\n", "\\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadMessages() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 7);
                if (parts.length == 7) {
                    Message m = new Message();
                    m.setId(Integer.parseInt(parts[0]));
                    m.setSenderId(Integer.parseInt(parts[1]));
                    m.setSenderType(parts[2]);
                    m.setReceiverId(Integer.parseInt(parts[3]));
                    m.setReceiverType(parts[4]);
                    m.setDate(java.time.LocalDateTime.parse(parts[5]));
                    m.setContent(parts[6].replace("\\n", "\n"));
                    messages.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadMessages() {
        synchronized (MessageService.class) {
            messages.clear();
            loadMessages();
        }
    }

    /**
     * Retourne le nombre total de messages (envoyés ou reçus) pour un enseignant donné
     * @param teacherId l'identifiant de l'enseignant
     * @return le nombre de messages
     */
    public int countMessagesForTeacher(int teacherId) {
        return (int) messages.stream()
                .filter(m -> (m.getSenderId() == teacherId && "TEACHER".equalsIgnoreCase(m.getSenderType()))
                        || (m.getReceiverId() == teacherId && "TEACHER".equalsIgnoreCase(m.getReceiverType())))
                .count();
    }
}