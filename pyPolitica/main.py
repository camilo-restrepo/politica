import re
import pymongo
import  numpy as np
from sklearn.ensemble import RandomForestClassifier
from array import *
from sklearn.feature_extraction.text import CountVectorizer
from pymongo import MongoClient

client = MongoClient("159.203.105.161", 27017)
db = client.boarddb
collection = db.trainingTweets
documents = collection.find()
negativeSet = []
neutralSet = []
positiveSet = []
polarity = "polarity"
dataset = []
for document in documents:
    tweetText = document["text"]
    cleanTweetText = re.sub(r'ht\S+', 'URL', tweetText, flags=re.MULTILINE)
    # tuple = [document[polarity], cleanTweetText]
    dataset.append({ "polarity" : document[polarity], "text": cleanTweetText })

vectorized = CountVectorizer(analyzer="word", tokenizer=None, preprocessor=None, stop_words=None, max_features=5000)
textList = []
sentimentList = []
for item in dataset:
    textList.append(item["text"])
    sentimentList.append(item[polarity])
trainingDataFeatures = vectorized.fit_transform(textList)

trainingDataFeatures = trainingDataFeatures.toarray()
print(trainingDataFeatures.shape)

vocab = vectorized.get_feature_names()
print(vocab)

dist = np.sum(trainingDataFeatures, axis=0)
for tag, count in zip(vocab, dist):
    print(count, tag)

forest = RandomForestClassifier(n_estimators=100)
forest = forest.fit(trainingDataFeatures, sentimentList)

result = forest.predict(trainingDataFeatures)
print(result)