import re
import pymongo
import  numpy as np
from nltk.corpus import stopwords
from nltk.tokenize import TweetTokenizer
from sklearn.ensemble import RandomForestClassifier
from array import *
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.externals import joblib
from pymongo import MongoClient

def review_to_wordlist(review, remove_stopwords=False):
    # Function to convert a document to a sequence of words,
    # optionally removing stop words.  Returns a list of words.
    # 1. Remove HTML
    review_text = re.sub(r'ht\S+', 'URL', review, flags=re.MULTILINE)
    # review_text = BeautifulSoup(review).get_text()
    # 2. Remove non-letters
    # review_text = re.sub("[^a-zA-Z]"," ", review_text)
    # 3. Convert words to lower case and split them
    words = review_text.lower().split()
    # 4. Optionally remove stop words (false by default)
    if remove_stopwords:
        stops = set(stopwords.words("spanish"))
        words = [w for w in words if not w in stops]
    # 5. Return a list of words
    return (' '.join(words))

tokenizer = TweetTokenizer()
def myTokenizer(string):
  return tokenizer.tokenize(string)

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
    cleanTweetText = review_to_wordlist(tweetText, True)
    # cleanTweetText = re.sub(r'ht\S+', 'URL', tweetText, flags=re.MULTILINE)
    dataset.append({ "polarity" : document[polarity], "text": cleanTweetText })

vectorized = CountVectorizer(analyzer="word", tokenizer=myTokenizer, preprocessor=None, stop_words=None, max_features=5000)
textList = []
sentimentList = []
originalDataset = dataset
dataset = originalDataset[0:2400]
test_dataset = originalDataset[2400:]
test_text_list = []
test_sentiment_list = []
for item in dataset:
    textList.append(item["text"])
    sentimentList.append(item[polarity])
trainingDataFeatures = vectorized.fit_transform(textList)

for item in test_dataset:
    test_text_list.append(item["text"])
    test_sentiment_list.append(item[polarity])
test_data_features = vectorized.transform(test_text_list)

trainingDataFeatures = trainingDataFeatures.toarray()
test_data_features = test_data_features.toarray()

# print(trainingDataFeatures.shape)

vocab = vectorized.get_feature_names()
# print(vocab)

dist = np.sum(trainingDataFeatures, axis=0)
'''
for tag, count in zip(vocab, dist):
  print(count, tag)
  '''

forest = RandomForestClassifier(n_estimators=100)
forest = forest.fit(trainingDataFeatures, sentimentList)

print(len(dataset))
print(len(test_dataset))
print(len(test_text_list))
print(len(test_sentiment_list))
result = forest.predict(test_data_features)
print(result)

counter = 0
print("result %d sentimentList %d", len(result), len(sentimentList))
for r in range(0, len(result)):
    if result[r] == test_sentiment_list[r]:
        counter += 1
print(counter)
print("Accuracy: %d", (counter/len(result)))

#joblib.dump(vectorized, "model-part-1/vectorized.pkl")
#joblib.dump(forest, "model-part-1/forest.pkl")

#-------------------------------------------------------------

