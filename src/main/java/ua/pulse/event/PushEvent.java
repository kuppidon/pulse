package ua.pulse.event;

import ua.pulse.bean.CourseBean;

/**
 * Created by Alex on 23.01.2017.
 */
public abstract class PushEvent {

    protected String session;
    protected CourseBean courseBean;

    public String getSession(){return  session;}
    public CourseBean getCourse(){return courseBean;}

}
