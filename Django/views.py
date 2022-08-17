from django.shortcuts import render
from django.http import HttpResponse
from .forms import SearchForm
import re

def readline(post_data, filepath):
    line_num = 0
    document = open(filepath, 'r')
    lines = document.readlines()
    tmp = ''
    count = []
    for line in lines:
        line_num = line_num+1
        if(re.findall(post_data, line, flags=re.IGNORECASE)):
            count.append(line_num)
    line_num2 = 0
    for line in lines:
        line_num2 = line_num2 + 1
        #nextline = lines[line_num2]
        if(re.findall(post_data, line, flags=re.IGNORECASE)):
            return tmp, line, count[:3]   #choose the first three
        tmp = line

    



# Create your views here.
def index(request):
    return HttpResponse("Hello world!")

def index1(request):
    context = dict()
    #create a dictionary called context
    if request.method == "POST":
        form = SearchForm(request.POST)
        if form.is_valid():
            post_data = form.cleaned_data['search']
            print(post_data)
        
        f = open('/home/uic/Desktop/result.txt')
        line = f.readline()

        filename=[]
        result=[]

        while line:
            #word, line = line.split(" ")
            a = re.split('[;	]',line)
            word = a[0]
            #line, TFIDF  = word.split(" +")
            if post_data == word:
                name=a[1]
                filename.append(name)
            line = f.readline()
        f.close()


        for i in filename:
            w1, w2, w3 = readline(post_data, "static/dataset/{kname}.txt".format(kname = i))

            result.append([i, w1, w2, w3])



        context['filename'] = filename




        context['result'] = result

        context['content'] = "Group_4 love UIC "+post_data
        #context['filename'] = _dict.get(post_data)# a dictionary file
        #post_data is "key", get(post_data) is value
        
        
        

        return render(request, 'history.html', context)
        #render: pass "context" to history.html
    else:
        #context['content'] = "Group_4 love DS!"
        form = SearchForm()
    context['form'] = form
    return render(request, 'index.html', context)
    #render: pass "context" to index.html












def load_dict_from_file(filepath):
    #_dict = {}
    try:
        with open(filepath, 'r') as dict_file:
            for line in dict_file:
                (key, value) = line.strip().split(',')
                #split every line to two parts (key & value) by '    '
                _dict[key] = value
    except IOError as ioerr:
        print ("file %s is not exist" % (filepath))     
    return _dict



#_dict = {}
#_dict = load_dict_from_file ('/home/uic/ws2_django/static/TF_IDF.txt')


