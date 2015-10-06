function [A,CUT] = segment(MuO,SigO2,MuB,SigB2)

global X N;

% Establish the force displacement for each pixel
for i=1:N
    for j=1:N
        PwO = (1/sqrt(2*pi*SigO2))*exp(-(X(i,j)-MuO).^2/(2*SigO2));
        PwB = (1/sqrt(2*pi*SigB2))*exp(-(X(i,j)-MuB).^2/(2*SigB2));
        A(i,j,1) = (PwO/(PwO+PwB))*100;
        A(i,j,2) = (PwB/(PwO+PwB))*100;
    end
end

% Regional Weight assignments
SigW2 = 20^2;

for i = 1:N
    for j = 1:N-1
        A(i,j,3) = exp(((-1)*(norm(X(i,j)-X(i,j+1)))^2)/(2*SigW2))*.01; % Vertical Influence
        A(j,i,4) = exp(((-1)*(norm(X(j,i)-X(j+1,i)))^2)/(2*SigW2))*.01; % Hirzontal Influence
    end
end

for i=2:N-1
    for j=2:N-1
        A(i,j,5) = (A(i,j,1)-A(i,j,2))...        % Probability forces
            + (X(i,j-1)-X(i,j))*A(i,j-1,4)...   % Above spring forces
            + (X(i,j+1)-X(i,j))*A(i,j,4)...     % Below spring forces
            + (X(i-1,j)-X(i,j))*A(i-1,j,3)...   % Left
            + (X(i+1,j)-X(i,j))*A(i,j,3);       % Right
    end
end


figure(4); %-----------------------------------------------------------------------
imagesc(A(:,:,5));
colormap(gray);

% Edge Spring energy
for i = 1:N
    for j = 1:N-1
        A(i,j,6) = abs((A(i,j,5)-A(i,j+1,5)));
        A(j,i,7) = abs((A(j,i,5)-A(j+1,i,5)));
    end
end

CUT = A(:,:,6)+A(:,:,7);
CUT = round(abs(CUT));
figure(5);
imagesc(CUT);
colormap(gray);