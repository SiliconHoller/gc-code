function [MuO,SigO2,MuB,SigB2] = Find_Stats()
global X N;

X = round(X);
% Build intensity histogram
x_hist = [0:1:255];
dist = zeros(1,256);
for i=1:N
    for j=1:N
        dist(X(i,j)+1) = dist(X(i,j)+1)+1;
    end
end

figure(2); %--------------------------------------------------------------------
plot(x_hist,dist);
title('Intensity Distribution');

% Get Statistical constants from user
MuO = input('Enter mean value for Object: ');
SigO = input('Enter approximate value for Objects deviation: ');
MuB = input('Enter mean value for Background: ');
SigB = input('Enter approximate value for Backgrounds deviation: ');

% Display Distributions for users review
SigO2 = SigO^2;
SigB2 = SigB^2;

PO = (1/sqrt(2*pi*SigO2))*exp(-(x_hist-MuO).^2/(2*SigO2));
PB = (1/sqrt(2*pi*SigB2))*exp(-(x_hist-MuB).^2/(2*SigB2));

%Probability distribution curve
figure(3); %--------------------------------------------------------------------
plot(x_hist,PO,x_hist,PB)
title('Probability Distribution');