package com.company.dental.modules.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.member.entity.MemberAccountEntity;
import com.company.dental.modules.member.query.MemberAccountPageQuery;
import com.company.dental.modules.member.vo.MemberAccountDetailVO;
import com.company.dental.modules.member.vo.MemberAccountPageItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberAccountMapper extends BaseMapper<MemberAccountEntity> {

    Page<MemberAccountPageItemVO> selectMemberAccountPage(Page<MemberAccountPageItemVO> page,
                                                          @Param("query") MemberAccountPageQuery query,
                                                          @Param("orgId") Long orgId,
                                                          @Param("accessScope") String accessScope,
                                                          @Param("clinicIds") List<Long> clinicIds,
                                                          @Param("staffId") Long staffId);

    MemberAccountDetailVO selectMemberAccountDetailById(@Param("id") Long id,
                                                        @Param("orgId") Long orgId,
                                                        @Param("accessScope") String accessScope,
                                                        @Param("clinicIds") List<Long> clinicIds,
                                                        @Param("staffId") Long staffId);

    @Select("""
            SELECT COUNT(1)
            FROM member_level
            WHERE id = #{levelId}
              AND org_id = #{orgId}
              AND status = 1
            """)
    Long countValidLevel(@Param("orgId") Long orgId, @Param("levelId") Long levelId);
}
