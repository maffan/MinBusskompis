package se.grupp4.minbusskompis;

import android.support.annotation.NonNull;

import org.junit.*;
import org.mockito.Mockito;

import se.grupp4.minbusskompis.BussParse.BussRelationMessenger;
import se.grupp4.minbusskompis.BussParse.BussSyncCodeGenerator;

import static org.junit.Assert.*;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussSyncCodeGeneratorTest {
    BussSyncCodeGenerator bussSyncCodeGenerator;
    @Before
    public void setUp() {
        bussSyncCodeGenerator = new BussSyncCodeGenerator(4);
    }



}