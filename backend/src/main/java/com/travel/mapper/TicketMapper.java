package com.travel.mapper;

import com.travel.entity.Ticket;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TicketMapper {
    List<Ticket> selectTicketList(@Param("ticket") Ticket ticket,
                                  @Param("start") Integer start,
                                  @Param("pageSize") Integer pageSize);

    int countTicket(@Param("ticket") Ticket ticket);

    Ticket selectTicketById(Integer id);

    int insertTicket(Ticket ticket);

    int updateTicket(Ticket ticket);

    int deleteTicket(Integer id);

    // 新增：扣减门票库存
    int updateStock(@Param("ticketId") Integer ticketId, @Param("num") Integer num);

    // 新增：订单取消，返还库存
    void addStock(@Param("ticketId") Integer ticketId, @Param("num") Integer num);

    // 新增：搜索联想，按门票名称模糊匹配，最多返回 10 条
    List<String> selectNamesByKeyword(@Param("keyword") String keyword);
}
