import re
import pymongo
import  numpy as np
from sklearn.ensemble import RandomForestClassifier
from array import *
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.externals import joblib
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

counter = 0
print("result %d sentimentList %d", len(result), len(sentimentList))
print(result[0])
print(sentimentList[0])
for r in range(0, len(result)):
    if result[r] == sentimentList[r]:
        counter += 1
print(counter)
print("Accuracy: %d", (counter/len(result)))

joblib.dump(vectorized, "model-part-1/vectorized.pkl")
joblib.dump(forest, "model-part-1/forest.pkl")
