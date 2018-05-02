LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Hardware
LOCAL_SRC_FILES := Hardware.cpp

include $(BUILD_SHARED_LIBRARY)