package com.ch.personmis.repository;

import com.ch.personmis.entity.Quit;
import java.util.List;

public interface QuitRepository {
    int addQuit(Quit quit);
    List<Quit> selectQuitStaff(Quit quit);
    List<Quit> selectQuitsByCon(Quit quit);
}
