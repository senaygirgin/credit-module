package com.mybank.credit_module.service.impl;

import jdk.dynalink.linker.LinkerServices;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class test {

    public static void main(String[] args) {
        List<Integer> l = new ArrayList<>(Arrays.asList(6,1,2,9,3,4,5));

        double installmentAmount = 2.5;
        double amount = 1.2;
        int payableInstallmentCount = (int) (amount / installmentAmount);

        System.out.println("payableInstallmentCount : " + payableInstallmentCount);

        long length = l.stream()
                .peek(i -> System.out.println("value1 : " + i))
                .sorted()
                .limit(payableInstallmentCount)
                .filter(e -> e < 4)
                .peek(i -> System.out.println("value : " + i))
                .count();

        System.out.println(length);


        LocalDate today = LocalDate.now();

        System.out.println( today.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));

    }
}
