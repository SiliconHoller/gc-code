% Initialization

clear;clc;

img = input('Image to be segmented: ');

global X N;
X = rload(img,[512,512]);

% Display Initial Image
figure(1); %-------------------------------------------------------------------
colormap(gray);
imagesc(X);

N = size(X);
N = N(1,1);

[MuO,SigO2,MuB,SigB2] = Find_Stats();

% First Run
[A,CUT] = segment(MuO,SigO2,MuB,SigB2);


% Option to preprocess
choice = menu('Run again with pre-processing?','yes','no');
while choice ~= 2
    rerun = 1;
    while rerun ~= 2
        reduce_noise();
        rerun = menu('Rerun cleaning?','yes','no');
    end
    [MuO,SigO2,MuB,SigB2] = Find_Stats();
    [A] = segment(MuO,SigO2,MuB,SigB2);
    choice = menu('Run again with pre-processing?','yes','no');
end