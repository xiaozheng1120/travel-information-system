package com.travel.service.impl;
import com.travel.entity.Ticket;
import com.travel.mapper.TicketMapper;
import com.travel.service.TicketService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketServiceImpl implements TicketService {

    @Resource
    private TicketMapper ticketMapper;

    @Override
    public Map<String, Object> getTicketPage(Integer pageNum, Integer pageSize, Ticket ticket) {
        Map<String,Object> res = new HashMap<>();
        int start = (pageNum -1)*pageSize;
        List<Ticket> list = ticketMapper.selectTicketList(ticket, start, pageSize);
        int total = ticketMapper.countTicket(ticket);
        res.put("records",list);
        res.put("total",total);
        return res;
    }

    @Override
    public boolean addTicket(Ticket ticket) {
        return ticketMapper.insertTicket(ticket) > 0;
    }

    @Override
    public boolean updateTicket(Ticket ticket) {
        return ticketMapper.updateTicket(ticket) >0;
    }

    @Override
    public boolean deleteTicket(Integer id) {
        return ticketMapper.deleteTicket(id) >0;
    }

    @Override
    public Ticket getTicketById(Integer id) {
        return ticketMapper.selectTicketById(id);
    }
}
