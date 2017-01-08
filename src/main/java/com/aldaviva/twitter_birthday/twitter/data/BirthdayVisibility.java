package com.aldaviva.twitter_birthday.twitter.data;

public enum BirthdayVisibility {

    PUBLIC(4),
    MY_FOLLOWERS(3),
    PEOPLE_I_FOLLOW(2),
    WE_FOLLOW_EACH_OTHER(1),
    ONLY_ME(0);

    private final int formValue;

    BirthdayVisibility(final int formValue) {
        this.formValue = formValue;
    }

    public int getFormValue() {
        return formValue;
    }
}
