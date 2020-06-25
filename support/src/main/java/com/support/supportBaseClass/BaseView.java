package com.support.supportBaseClass;

public interface BaseView<T> {

    void setPresenter(T presenter);

    void showProcessing();

    void hideProcessing();

    void showMessage(String message);

    void showMessage(int message);

    void showErrorMessage(String message);

    void showErrorMessage(int message);
}
