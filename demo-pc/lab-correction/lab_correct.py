import cv2
import numpy as np

def lab_color_correction(img, strength=1.2, mode=0):
    lab = cv2.cvtColor(img, cv2.COLOR_BGR2LAB)
    L, a, b = cv2.split(lab)
    if mode == 0:
        a = np.clip(a - strength * 18, 0, 255).astype(np.uint8)
    else:
        a = np.clip(a + strength * 18, 0, 255).astype(np.uint8)
    lab_new = cv2.merge((L, a, b))
    return cv2.cvtColor(lab_new, cv2.COLOR_LAB2BGR)

def nothing(x):
    pass

if __name__ == "__main__":
    # 强制使用图片模式，避免摄像头调用失败
    test_img = cv2.imread("test.jpg")
    if test_img is None:
        print("错误：请将 test.jpg 放在和代码同一个文件夹里")
        exit()

    cv2.namedWindow("Lab_Correction")
    cv2.createTrackbar("Strength", "Lab_Correction", 10, 30, nothing)
    cv2.createTrackbar("Mode_0Protan_1Deutan", "Lab_Correction", 0, 1, nothing)
    cv2.createTrackbar("Switch_0Origin_1Correct", "Lab_Correction", 1, 1, nothing)

    while True:
        strength = cv2.getTrackbarPos("Strength", "Lab_Correction") / 10
        mode = cv2.getTrackbarPos("Mode_0Protan_1Deutan", "Lab_Correction")
        switch = cv2.getTrackbarPos("Switch_0Origin_1Correct", "Lab_Correction")

        src = test_img.copy()
        if switch == 1:
            result = lab_color_correction(src, strength, mode)
        else:
            result = src

        cv2.imshow("Lab_Correction", result)
        key = cv2.waitKey(1) & 0xFF
        if key == ord('q'):
            break

    cv2.destroyAllWindows()
