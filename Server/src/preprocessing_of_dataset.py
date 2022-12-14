# -*- coding: utf-8 -*-
"""Preprocessing_Of_Dataset.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1nBHwppTP7sL48wM4ypPdBTFNhPurJtEC
"""

import pandas as pd
import numpy as np

df = pd.read_csv("/content/netflix_titles.csv")

df.head()

df1 = df.dropna().reset_index(drop=True)

df1.head()

for i in range (0,len(df1['type'])):
  if(df1['type'][i]=='Movie'):
    df1['type'][i]=1
  else:
    df1['type'][i]=2

for i in range (0,len(df1['duration'])):
  if('min' in str(df1['duration'][i])):
    df1['duration'][i]=1
  else:
    df1['duration'][i]=int(df1['duration'][i][0])

print(df1['duration'].unique())

df1.rename(columns = {'listed_in':'genre'}, inplace = True)

df1.to_excel("ProcessesData.xls")

df1['show_id'] = [sub.replace('s', '') for sub in df1['show_id']]

import re

df1['duration'] = [sub.replace(re.findall(r'\b\d{2,3}\b'), '1') for sub in df1['duration']]

for i in range (0,len(df['show_id'])):
  df['show_id'][i]= df['show_id'][i][1:]

for i in range (0,len(df['show_id'])):
  df1['show_id'][i]= df['show_id'][i][1:]

