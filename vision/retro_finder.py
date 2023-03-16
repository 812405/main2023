import cv2 as cv
import numpy as np
import imutils
import msgpack


class RetroFinder:

    def __init__(self, width, height, threshold, threshmode):
        self.threshold = threshold
        self.threshmode = threshmode
        self.horizontal_offset = width/2
        self.vertical_offset = height/2
        # TODO: tune this value

    def find(self, img):
        _, thresh = cv.threshold(img, self.threshold, 255, self.threshmode)
        contours, hierarchy = cv.findContours(
            thresh, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(contours)

        tapes = {}
        tapes["tapes"] = []

        for c in cnts:
            mmnts = cv.moments(c)
            cX = int(mmnts["m10"] / mmnts["m00"]) - self.horizontal_offset
            cY = int(mmnts["m01"] / mmnts["m00"]) - self.vertical_offset
            tapes["tapes"].append(
                {
                    "centroid": [cX, cY]
                }
            )

        cv.drawContours(img, [c], -1, (0, 255, 0), 2)
        cv.circle(img, (cX, cY), 7, (255, 255, 255), -1)
        cv.putText(img, "centroid", (cX - 20, cY - 20),
                   cv.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
        cv.imshow(img)

        return tapes