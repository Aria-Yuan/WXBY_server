
# coding: utf-8

# In[38]:


import jieba
import logging
import sys
import codecs
import traceback
import pandas as pd
import numpy as np
import re
from collections import Counter
from pymongo import MongoClient


from lightgbm import LGBMRegressor
from sklearn.model_selection import train_test_split
from sklearn import linear_model
from sklearn.linear_model import *
from sklearn.neighbors import KNeighborsRegressor
from sklearn.metrics import mean_squared_error
from sklearn import *
from catboost import CatBoostRegressor
from sklearn.metrics import roc_auc_score  
from sklearn.model_selection import train_test_split  
import math
import lightgbm as lgb 
import xgboost as xgb 
import random
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import LabelEncoder


# In[39]:


if sys.version_info[0] > 2:
    is_py3 = True
else:
    reload(sys)
    sys.setdefaultencoding("utf-8")
    is_py3 = False


# In[40]:


def delCNf(line):
    regex = re.compile('[^\u4e00-\u9fa5a-zA-Z0-9\s]')
    return regex.sub('', line)


# In[41]:


def str_replace(str_source, char, *words):
    str_temp = str_source
    for word in words:
        str_temp = str_temp.replace(word, char)
    return str_temp


# ## 匯入

# In[42]:


def load_userdictfile(dict_file):
    jieba.load_userdict(dict_file)


# In[43]:


def load_processfile(process_file):
    corpus_list = []
    try:
        fp = open(process_file, "r")
        for line in fp:
            conline = line
            corpus_list.append(conline)
        return True, corpus_list
    except:
        logging.error(traceback.format_exc())
        return False, "get process file fail"


# In[44]:


def output_file(out_file, item):
    try:
        fw = open(out_file, "a")
        fw.write('%s' % (item.encode("utf-8")))
        fw.close()
    except:
        logging.error(traceback.format_exc())
        return False, "out file fail"


# ## 分词

# In[45]:


def delCNf(line):
    regex = re.compile('[^\u4e00-\u9fa5a-zA-Z\s]')
    return regex.sub('', line)


# In[46]:


def str_replace(str_source, char, *words):
    str_temp = str_source
    for word in words:
        str_temp = str_temp.replace(word, char)
    return str_temp


# In[47]:


def seg_words(sentence):
    stopwords = {}.fromkeys([ line.rstrip() for line in open('D:/Program Files/WeChat/WeChat Files/zy-hahu/Files/stopword.txt') ])
    segs = jieba.cut(sentence, cut_all=False)  # 默认是精确模式
    return " ".join(segs)     # 分词，然后将结果列表形式转换为字符串


# In[81]:


def segmentation(file):
    files = re.split(",", file)
    lst = []
    for line in files:
        line = delCNf(line)
        line = str_replace(line, "", "\t", "\n", " ", "\u3000", "○")
        seg_list = jieba.cut(line, cut_all = False)
        words = " ".join(seg_list)
        words = "".join(words.split('\n')) # 去除回车符
        words = delCNf(words)
        word = re.split(r" ", words)
        lst.extend(word)
    return lst


# In[82]:

conn = MongoClient('mongodb://dajiayiqibiye:wxby@wxby-shard-00-00-7ea9c.mongodb.net:27017,wxby-shard-00-01-7ea9c.mongodb.net:27017,wxby-shard-00-02-7ea9c.mongodb.net:27017/test?ssl=true&replicaSet=WXBY-shard-0&authSource=admin&retryWrites=true')#改成本機ip
db = conn.wxbyt
case_set = db.case_consult

#string = '事實及理由上訴人經合法通知,未於言詞辯論期日到場,核無民事訴訟法第386條所列各款情形,爰依被上訴人聲請,由其一造辯論而為判決二、被上訴人起訴主張:上訴人於民國101年11月1日,向被上訴人承租新北市○區∞o路00o0號3樓房屋(下稱系爭房屋),租期1年即自101年11月1日起至102年10月31日止,租金每月新台幣(下同)1萬2,000元,於每月1日前給付。嗣租期屆滿,上訴人拒絕遷讓返還系爭房屋,並累計積欠租金3萬6,θθ0元未付,迭經被上訴人催討,上訴人均置之不理。又本件租期既已屆滿,上訴人即屬無權占有系爭房屋,另應自102年11月1日起至返還房屋之日止,按月賠償被上訴人相當於未收租金額計算之損害金。爰依租賃及不當得利之法律關係,求為判決∶上訴人應將系爭房屋全部遷讓返還被上訴人,並應給付被上訴人3萬6,000元,暨應自102年11月1日起至返還房屋之日止,按月給付被上訴人1萬2,000元(原審判決上訴人敗訴,並依職權為准、免假執行宣告,上訴人不服提起上訴)。並答辯聲明:上訴駁回。三、上訴人則以:被上訴人未給與上訴人搬遷費用,上訴人無法遷讓返還系爭房屋等語,資為抗辯。並上訴聲明:(一)原判決廢棄;(二)被上訴人在第一審之訴駁回。四、被上訴人主張上訴人於101年11月1日,向被上訴人承租系爭房屋,租期1年至102年10月31日止,租金每月1萬2,000元,嗣租期屆滿,上訴人拒絕遷讓返還系爭房屋,並累計積欠租金3萬6,000元未付,且無權占有系爭房屋,致被上訴人受有每月相當租金額之不當得利損害金,被上訴人自得依租賃及不當得利之法律關係,請求上訴人將系爭房屋全部遷讓返還被上訴人,並給付租金3萬6,000元,暨自102年11月1日起至返還房屋之日止,按月給付不當得利損害金1萬2,000元等情,有租賃契約書在卷可稽(原審司板簡調字卷第6、7頁)且為上訴人所不爭執,自堪信為真實,並屬於法有據。上訴人雖抗辯被上訴人未給與上訴人搬遷費用,上訴人無法遷讓返還系爭房屋云云,惟所辯尚屬如何遷讓房屋之執行問題,自不能據以對抗被上訴人。五、從而,被上訴人依租賃及不當得利之法律關係,求為判決上訴人應將系爭房屋全部遷讓返還被上訴人,並應給付被上訴人3萬6,000元,暨應自102年11月1日起至返還房屋之日止按月給付被上訴人1萬2,000元,自屬應予准許。原審為上訴人敗訴之判決,核無違誤,上訴論旨指摘原判決不當,求予廢棄改判,為無理由六、兩造其餘之攻擊或防禦方法及未經援用之證據,經本院斟酌後,認為均不足以影響本判決之結果,自無逐一詳予論駁之必要,併此敘明。七、據上論結,本件上訴為無理由,依民事訴訟法第436條之1第3項、第463條、第385條第1項前段、第449條第1項、第78條判決如主文'
a = sys.argv[1]
condition = {'id': a}
string = case_set.find_one(condition).get("content")


matrix = pd.read_csv("D:/Program Files/WeChat/WeChat Files/zy-hahu/Files/matrix_column.csv")
matrix.loc[0] = random.randint(0,0)


# In[173]:


for col in matrix.columns:
    count = 0
    for word in segmentation(string):
        if str(word) == str(col):
            count += 1
    matrix.loc[0, col] = count


# In[174]:


predict_x = matrix.astype('int')
predict_x


# In[190]:


gbm_martix = pd.read_csv("D:/Program Files/WeChat/WeChat Files/zy-hahu/Files/matrix1500.csv")
df = gbm_martix.dropna()
X = df.drop(['result'], axis=1)
Y = df['result']


# ## gbm模型

# In[191]:


def gbm():
    train_x, test_x, train_y, test_y = train_test_split(X, Y, test_size = 0.2, random_state = 5)
    
    train_x = train_x.drop(['filename'], axis=1).dropna().astype('int')
    test_x = test_x.drop(['filename'], axis=1).dropna().astype('int')
    train_y = train_y.dropna()
    test_y = test_y.dropna()
        
    lgb_train = lgb.Dataset(train_x, train_y)  
    lgb_test = lgb.Dataset(test_x, test_y, reference=lgb_train)
        
    lgb_train = lgb.Dataset(train_x, train_y)  
    params = {  
        'boosting_type': 'gbdt',  
        'objective': 'multiclass',  
        'num_class': 3,  
        'metric': 'multi_error',  
        'num_leaves': 300,  
        'min_data_in_leaf': 100,  
        'learning_rate': 0.01,  
        'feature_fraction': 0.8,  
        'bagging_fraction': 0.8,  
        'bagging_freq': 5,  
        'lambda_l1': 0.4,  
        'lambda_l2': 0.5,  
        'min_gain_to_split': 0.2,  
        'verbose': 5,  
        'is_unbalance': True  
    } 
  
    gbm = lgb.train(params,  
                    lgb_train,  
                    num_boost_round = 10000,  
                    valid_sets = lgb_test,  
                    early_stopping_rounds = 500) 
        
#         y_pred = gbm.predict(predict_x)
#         y_pred = [list(x).index(max(x)) for x in y_pred]
    return gbm


# In[192]:


gbm = gbm()
print("??????????????")

# In[193]:


lgbm_predict = gbm.predict(predict_x)
lgbm_predict = [list(x).index(max(x)) for x in lgbm_predict]
lgbm_predict

old_one = {"id": case_set.find_one(condition).get("id")}
new_one = {"$set": { "result": lgbm_predict[0] }}
case_set.update_one(old_one, new_one)
# 0 勝，1 平， 2 敗訴


# ## KNN 模型

# In[198]:


def knn(predict_x, n):
    df_dataframe = pd.DataFrame(df)

    knn_train_x = X.drop(['filename'], axis=1).dropna().astype('int')
    knn_train_y = Y.dropna()
    
    KNN = KNeighborsClassifier(algorithm='auto', leaf_size=30, metric='minkowski',
                n_jobs=1, n_neighbors=5, p=2, weights='distance')
    KNN.fit(knn_train_x, knn_train_y)
    predict = KNN.predict(predict_x)
    
    neighborpoint = KNN.kneighbors(predict_x, n, False)
    
    neighborlst = []
    for idx in neighborpoint[0]:
        neighborlst.append(df_dataframe.loc[idx,'filename'])
        
    return neighborlst, predict


# In[200]:


neighborlst, knn_predict = knn(predict_x, 5)

# In[201]:

neighborlst
#print(neighborlst)
old_one = {"id": case_set.find_one(condition).get("id")}
new_one = {"$set": { "neighborlst": neighborlst}}
case_set.update_one(old_one, new_one)

# In[202]:

knn_predict
#print(knn_predict)
