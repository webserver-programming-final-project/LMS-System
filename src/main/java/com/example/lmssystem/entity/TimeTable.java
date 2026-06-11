package com.example.lmssystem.entity;

public class TimeTable {
    private Long id;
    private Long classId;
    private Long slotId;
    private String days;
    private int slotNo;
    private String startTime;
    private String endTime;

    public TimeTable() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getDays() { return days; }
    public void setDays(String days) { this.days = days; }

    public int getSlotNo() { return slotNo; }
    public void setSlotNo(int slotNo) { this.slotNo = slotNo; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
