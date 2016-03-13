import nltk.data
import pymongo
from pymongo import MongoClient
from gensim.models import word2vec

from bs4 import BeautifulSoup
import re
from nltk.corpus import stopwords

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
    return(words)

# Define a function to split a review into parsed sentences
def review_to_sentences(review, tokenizer, remove_stopwords=False):
    raw_sentences = tokenizer.tokenize(review.strip())
    sentences = []
    for raw_sentence in raw_sentences:
        if len(raw_sentence) > 0:
          sentences.append(review_to_wordlist(raw_sentence, remove_stopwords))
    return sentences

tweets = []
client = MongoClient("159.203.105.161", 27017)
db = client.boarddb
collection = db.sentimentClassifiedTweets
documents = collection.find().limit(100000)
for document in documents:
    tweetText = document["text"]
    tweets.append(tweetText)

tokenizer = nltk.data.load('tokenizers/punkt/spanish.pickle')

sentences = []
for tweet in tweets:
  sentences += review_to_sentences(tweet, tokenizer, False)

# Params
numFeatures = 300
minWordCount = 20
numWorkers = 6
context = 10
downSampling = 1e-3

model = word2vec.Word2Vec(sentences, workers=numWorkers, size=numFeatures, min_count=minWordCount, window=context, sample=downSampling)
model.init_sims(replace=True)
modelName = "model-part-2/word2VecModel"
model.save(modelName)

print(model)
print(model.most_similar("peñalosa"))
