from gtts import gtts
import os

from moviepy.editor import AudioFileClip,concatenate_audioclips
from pydub import *
input_audio1=AudioFileClip("pause3s.mp3")
lang="en"
mytext="how are you? I am Fine what about you?Thank you."
box=mytext.split('?')
count=0
total_count=0
print(box)
audio_list=[]
play_list=[]
for s in box:
    sound=gTTS(text=s, lang=lang, slow=False)
    file_name="file_"+str(count)+".mp3"
    sound.save(file_name)
    _audio=AudioFileClip(file_name)
    combined=concatenate_audioclips([_audio,input_audio1])
    combined.write_audiofile(file_name)
    audio_list.append(file_name)
    total_count+=count
    count+=1
for i in range(0,total_count)
    object=AudioFileClip("file_"+str(i)+".mp3")
    playList.append(object)
print(playList)
final_audio=concatenate_audioclips([*playList])
final_audio.write_audiofile("output.mp3")