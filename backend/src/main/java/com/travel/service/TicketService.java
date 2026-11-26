package com.travel.service;
import com.travel.entity.Ticket;
import java.util.Map;

public interface TicketService {
    Map<String,Object> getTicketPage(Integer pageNum, Integer pageSize, Ticket ticket);
    boolean addTicket(Ticket ticket);
    boolean updateTicket(Ticket ticket);
    boolean deleteTicket(Integer id);
    Ticket getTicketById(Integer id);
}
