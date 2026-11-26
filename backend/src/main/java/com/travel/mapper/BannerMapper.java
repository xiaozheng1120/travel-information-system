package com.travel.mapper;

import com.travel.entity.Banner;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BannerMapper {
    /**
     * 分页查询banner列表
     * @param banner 查询条件
     * @param start 起始下标
     * @param pageSize 每页条数
     * @return banner列表
     */
    List<Banner> selectBannerList(@Param("banner") Banner banner,
                                  @Param("start") Integer start,
                                  @Param("pageSize") Integer pageSize);

    /**
     * 统计banner总条数（分页用）
     * @param banner 查询条件
     * @return 总数
     */
    int countBanner(@Param("banner") Banner banner);

    /**
     * 根据id查询单条banner
     */
    Banner selectBannerById(Integer id);

    /**
     * 新增banner
     */
    int insertBanner(Banner banner);

    /**
     * 修改banner
     */
    int updateBanner(Banner banner);

    /**
     * 根据id删除banner
     */
    int deleteBanner(Integer id);
}
