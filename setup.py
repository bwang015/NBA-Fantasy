#!/usr/bin/python

import os, sys

stats = sys.argv[1]
week = sys.argv[2]

if os.path.isfile("Export.xls"):
	os.remove("Export.xls")

if os.path.isfile("Week.xls"):
	os.remove("Week.xls")

os.rename(stats, "Export.xls")
os.rename(week, "Week.xls")

print "Ready to move to Eclipse"
