package com.company.dental.modules.org.vo;

import lombok.Data;

@Data
public class RoomVO {

    private Long id;
    private Long clinicId;
    private String roomCode;
    private String roomName;
    private String roomType;
    private String floorNo;
    private Integer status;
}
