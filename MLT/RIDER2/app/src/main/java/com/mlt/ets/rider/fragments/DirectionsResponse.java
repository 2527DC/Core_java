package com.mlt.ets.rider.fragments;
import java.util.List;

public class DirectionsResponse {
    public List<Route> routes;

    public static class Route {
        public List<Leg> legs;

        public static class Leg {
            public List<Step> steps;

            public static class Step {
                public String html_instructions;
                public Duration duration;
                public Distance distance;

                public static class Duration {
                    public String text;
                    public int value;
                }

                public static class Distance {
                    public String text;
                    public int value;
                }
            }
        }
    }

    public static class OverviewPolyline {
        public String points;
    }
}