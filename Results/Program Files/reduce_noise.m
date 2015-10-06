function reduce_noise()
% performs an blurring effect on the image to remove noise
global X;


for i=1:512
    for j=1:512
        X(i,j) = clique(X,i,j);
    end
end
figure(1);
imagesc(X);
colormap(gray);